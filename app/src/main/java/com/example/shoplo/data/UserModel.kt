package com.example.shoplo.data



class UserModel {
    var id: String? = null
    var profileImg: String? = null
    var username: String? = null
    var email: String? = null
    var phone: String? = null
    var bio: String? = null

    constructor()

    constructor(id: String?, username: String?, email: String?, phone: String?) {
        this.id = id
        this.username = username
        this.email = email
        this.phone = phone
    }

    constructor(id: String?, profileImg: String?, username: String?, email: String?, phone: String?, bio: String?) {
        this.id = id
        this.profileImg = profileImg
        this.username = username
        this.email = email
        this.phone = phone
        this.bio = bio
    }
}
