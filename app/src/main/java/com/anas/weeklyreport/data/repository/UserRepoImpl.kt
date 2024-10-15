package com.anas.weeklyreport.data.repository


import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.AppData.loggedInUser
import com.anas.weeklyreport.AppData.userToken
import com.anas.weeklyreport.ResultShort
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.presentaion.home.initGoogleSignInClient
import com.anas.weeklyreport.remote.UserApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val context: Context,
    private val userApi: UserApi,
    private val sharedPreferences: SharedPreferences
) :UserRepository {

    private val userKey = "logged_in_user"
    private val tokenKey = "user_token"
    override suspend fun signUp(user: User): Flow<Result<User>> = flow {
      emit(Result.Loading)
        try {

            val res = userApi.createNewUser(user)
            if (res.data != null){
                val gson = Gson()
                val json = gson.toJson(res.data!!.user)
                sharedPreferences.edit().putString(userKey, json).apply()
                sharedPreferences.edit().putString("user_token", res.data!!.token).apply()
                emit(Result.Success(res.data!!.user))
                loggedInUser = res.data!!.user
                userToken = res.data!!.token
            }else{
                emit(Result.Error(Exception()))
            }
        }catch (e:Exception){
            val googleSignInClient = initGoogleSignInClient(context)
            googleSignInClient.signOut()
            e.printStackTrace()
            emit(Result.Error(e))
        }
    }
    override suspend fun signOut() {
        try {
            val googleSignInClient = initGoogleSignInClient(context)
            googleSignInClient.signOut()
            loggedInUser = null
            userToken = null
            sharedPreferences.edit().remove(userKey).apply()
            sharedPreferences.edit().remove(tokenKey).apply()

        }catch (e:Exception){
            Log.e("sign out", e.message, e)
        }
    }

    override suspend fun updateUser(user: User): Flow<Result<User>> = flow{
        emit(Result.Loading)
        try {
            if (loggedInUser != null && loggedInUser!!.id.isNotBlank()){
                val res = userApi.updateUser(user, "Bearer $userToken")
                if (res.data == true){
                    updateLoggedInUser(user)
                    emit(Result.Success(loggedInUser!!))
                }else{
                    emit(Result.Error(Exception(res.message)))
                }
            }else{
                updateLoggedInUser(user)
                emit(Result.Success(loggedInUser!!))
            }
        }catch (e:Exception){
            Log.e("update user", e.message, e)
            emit(Result.Error(e))
        }
    }

    private fun updateLoggedInUser(user:User){
        val gson = Gson()
        val json = gson.toJson(user)
        sharedPreferences.edit().putString(userKey, json).apply()
        loggedInUser = if (loggedInUser == null){
            user
        }else{
            loggedInUser?.copy(
                fullName = user.fullName,
                company = user.company,
                email = user.email,
                ausbildungDepartment = user.ausbildungDepartment,
            )
        }
    }
    override fun getUser(): User? {
        return try {
            val signedInUser = GoogleSignIn.getLastSignedInAccount(context)
            val gson = Gson()
            val userString = sharedPreferences.getString(userKey, "")
            val userTokenString = sharedPreferences.getString(tokenKey, "")
            userToken = userTokenString
            loggedInUser = gson.fromJson(userString, User::class.java)
            signedInUser?.let {
                loggedInUser?.id = it.id!!
                loggedInUser?.email = it.email!!
            }
            loggedInUser
        }catch (e:Exception){
            Log.e("get user", e.message, e)
            null
        }
    }

    override suspend fun deleteUserById(id: String): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
           val res = userApi.deleteUser(id, "Bearer $userToken")
            if(res.data!!){
                signOut()
                emit(Result.Success(true))
            }else{
                emit(Result.Error(Exception(res.message)))
            }
        }catch (e:Exception) {
            Log.e("update user", e.message, e)
            emit(Result.Error(e))
        }
    }

    override suspend fun checkIfUserExist(id: String): Flow<Result<Boolean>> = flow{
        emit(Result.Loading)
        try {
            val res = userApi.checkIfUserExist(id)
            emit(Result.Success(res.data!!))
        }catch (e:Exception) {
            Log.e("update user", e.message, e)
            emit(Result.Error(e))
        }
    }
}