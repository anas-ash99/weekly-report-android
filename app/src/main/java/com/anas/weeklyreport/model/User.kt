package com.anas.weeklyreport.model

data class User(
    var id:String ="",
    var firstName:String= "",
    var lastName:String = "",
    var email:String = "",
    var emailVerified:Boolean = false,
    var passwordHash:String = "",
    var ausbildungDepartment:String ="",
    var company:String ="",
    var createdAt:String = ""
)
