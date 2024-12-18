package com.submission.submissionstoryapp.view.main

import com.submission.submissionstoryapp.data.network.story.ListStoryItem

object DataDummy {
    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val item = ListStoryItem(
                photoUrl = "https://dummyimage.com/640x4:3/$i",
                createdAt = "2024-12-18T10:00:00Z",
                name = "Author $i",
                description = "Description $i",
                lon = 106.816635,
                id = i.toString(),
                lat = -6.595038
            )
            items.add(item)
        }
        return items
    }
}