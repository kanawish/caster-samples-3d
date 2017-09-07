/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/** A Camera with perspective projection.
 *
 * @author mzechner */
public class PerspectiveCamera extends Camera {

    final Vector3 tmp = new Vector3();
    /** the field of view of the screenHeight, in degrees **/
    public float fieldOfView = 67;

    public PerspectiveCamera(int pixelWidth, int pixelHeight) {
        super(pixelHeight, pixelWidth);
    }

    /** Constructs a new {@link PerspectiveCamera} with the given field of view and viewport size. The aspect ratio is derived from
     * the viewport size.
     *
     * @param fieldOfViewY the field of view of the screenHeight, in degrees, the field of view for the width will be calculated
     *           according to the aspect ratio.
     * @param viewportWidth the viewport width
     * @param viewportHeight the viewport screenHeight */
    public PerspectiveCamera(int pixelWidth, int pixelHeight, float fieldOfViewY, float viewportWidth, float viewportHeight) {
        super(pixelHeight, pixelWidth);
        this.fieldOfView = fieldOfViewY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        update();
    }

    @Override
    public void update() {
        update(true);
    }

    @Override
    public void update(boolean updateFrustum) {
        float aspect = viewportWidth / viewportHeight;
        projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);

        if (updateFrustum) {
            invProjectionView.set(combined);
            Matrix4.inv(invProjectionView.val);
            frustum.update(invProjectionView);
        }
    }
}
