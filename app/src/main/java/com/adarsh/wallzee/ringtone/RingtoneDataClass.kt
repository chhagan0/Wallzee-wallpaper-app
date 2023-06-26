package com.adarsh.wallzee.ringtone

data class RingtoneDataClass(
    var ringtoneid: Int? = null,
    var ringtoneName: String? = null,
    var ringtoneDuration: Int? = null,
    var ringtoneImage: String? = null,
    var ringtoneUrl: String? = null,
    var downloads: Int = 1,
    var dateUploaded: String? = null,
    var ringtoneSize: Int? = null,
    var creatorName: String? = null,
    var creatorEmail: String? = null,
    var creatorVerified: Boolean = false,
)
