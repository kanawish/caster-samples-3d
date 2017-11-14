package com.kanawish.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 2017-10-02.
 */
@Singleton
class FirebaseManager @Inject constructor() {

    sealed class State {
        object Initializing : State()
        class Anonymous(private val regFun: (String) -> Unit) : State() {
            fun register(email: String) = regFun
        }
        object Registering : State()
        object Registered : State()
        data class Error(val errMsg: String) : State()
    }

    fun state(): Observable<State> = Observable.create { e ->
        val fb = FirebaseAuth.getInstance()

        // Monitors ongoing auth state.
        fun postInitFlow() {
            val listener = FirebaseAuth.AuthStateListener { fbAuth ->
                val currentUser = fbAuth.currentUser
                when {
                    currentUser == null -> {
                        e.onNext(State.Error("currentUser is now null. User likely force-logged out."))
                        e.onComplete()
                    }
                    currentUser.isAnonymous -> e.onNext(State.Anonymous { email ->
                        if (email.isEmpty()) return@Anonymous
                        e.onNext(State.Registering)

                        val credential = EmailAuthProvider.getCredential(email, UUID.randomUUID().toString())
                        currentUser
                                .linkWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        val errMsg = task.exception?.localizedMessage ?: "Undefined error while registering user."
                                        e.onNext(State.Error(errMsg))
                                    }
                                    // NOTE: If it works, authStateLister will be re-triggered, and we'll end up at the Registered state.
                                }
                    })
                    else -> e.onNext(State.Registered)
                }

            }

            e.setCancellable { fb.removeAuthStateListener(listener) }
            fb.addAuthStateListener(listener)
        }

        // Do we need to anonymously sign in?
        if (fb.currentUser == null) {
            e.onNext(State.Initializing)
            fb.signInAnonymously().addOnCompleteListener { task ->
                // On success, add the post init listener.
                if (task.isSuccessful && task.result.user != null) {
                    postInitFlow()
                } else {
                    e.onNext(State.Error("Error call to fb.signInAnonymously()"))
                    e.onComplete()
                }
            }
        } else {
            // We already had a user on hand, we jump straight to postInitFlow
            postInitFlow()
        }
    }

}