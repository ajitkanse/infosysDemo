package com.ajit.demoproj.data.api

import com.ajit.demoproj.data.local.Post

data class ApiPostResp(val rows: List<Post>, val title: String)