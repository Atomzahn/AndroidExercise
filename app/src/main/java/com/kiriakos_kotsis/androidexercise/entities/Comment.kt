package com.kiriakos_kotsis.androidexercise.entities

/**
 * This class represents a comment.
 * @param id        ID of this comment.
 * @param post_id   Corresponding Post ID.
 * @param name      Author of this comment.
 * @param email     E-mail address of the author of this comment.
 * @param comment   This comments text.
 */
class Comment (val id:Int, val post_id:Int, val name:String, val email:String, val comment:String)