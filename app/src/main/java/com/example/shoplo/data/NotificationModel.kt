package com.example.shoplo.data

class NotificationModel {
    var userId: String? = null
    var text: String? = null
    var postId: String? = null
    var isPost = false
    var isComment = false

    constructor() {}

    constructor(userId: String?, text: String?, postId: String?, isPost: Boolean, isComment: Boolean) {
        this.userId = userId
        this.text = text
        this.postId = postId
        this.isPost = isPost
        this.isComment = isComment
    }
}