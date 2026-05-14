package com.littleapp.videoplayer

class VideoFiles {
    var id: String? = null
    var path: String? = null
    var title: String? = null
    var fileName: String? = null
    var size: String? = null
    var dateAdded: String? = null
    var duration: String? = null

    constructor()

    constructor(
        id: String?, path: String?, title: String?, fileName: String?,
        size: String?, dateAdded: String?, duration: String?
    ) {
        this.id = id
        this.path = path
        this.title = title
        this.fileName = fileName
        this.size = size
        this.dateAdded = dateAdded
        this.duration = duration
    }
}