package com.example.apkprtmuan11.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class PostRequestTest {
    @Test
    fun patchRequestOmitsUnchangedBody() {
        assertEquals("""{"title":"Mesin A-01"}""", Gson().toJson(PostRequest(title = "Mesin A-01")))
    }
}
