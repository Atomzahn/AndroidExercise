package com.kiriakos_kotsis.androidexercise.entities

import java.io.Serializable

/**
 * Class that represents a Post.
 * @param id        ID of the Post.
 * @param author    Name of the author of this Post.
 * @param title     Title of the Post.
 * @param thumbnail URL to the thumbnail file.
 * @param image     URL to the image file.
 * @param content   Text of this Post.
 */
class Post (val id:Int, val author:String, val title:String, val thumbnail:String, val image:String, val content:String) : Serializable