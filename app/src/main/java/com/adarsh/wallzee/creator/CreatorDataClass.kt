package com.adarsh.wallzee.creator

data class CreatorDataClass(
    var creatorName: String? = null,
    var creatorLinks: String? = null,
    var creatorEmail: String? = null,
    var creatorProfileUrl: String? = null,
    var followers: Int? = null,
    var followingList: ArrayList<String>? = null,
    var isVerified: Boolean = false,
    var admin : Boolean = false
)
