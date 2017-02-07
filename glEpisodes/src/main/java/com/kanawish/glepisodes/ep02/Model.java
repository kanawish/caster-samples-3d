package com.kanawish.glepisodes.ep02;

import java.nio.FloatBuffer;

/**
 * A Model is a collection of vertices.
 *
 * As per <a href="https://en.wikipedia.org/wiki/Vertex_(geometry)#Vertices_in_computer_graphics">Wikipedia</a>,
 * a vertex, in the context of computer graphics, is a bundle of information. For each vertex, we
 * bundle 3d coordinates, color, texture coordinates, etc.
 *
 * Model coordinates are defined relative to their 'own' origin point (0,0,0). This is called model space.
 *
 * This implies that, to position a model into the world, we will need to apply a transformation to all of its
 * vertex coordinates.
 *
 * This transformation is stored below in the 'modelMatrix'.
 */
public class Model {

    FloatBuffer vertices ;

    float[] modelMatrix ;


}
