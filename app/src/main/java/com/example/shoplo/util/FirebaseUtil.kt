package com.example.shoplo.util



import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

object FirebaseUtil {

    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun isLoggedIn(): Boolean {
        return currentUserId() != null
    }

    fun currentUserDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("CurrentUser").document(currentUserId()!!)
    }

    fun allUserCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("CurrentUser")
    }

    fun getChatroomReference(chatroomId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId)
    }

    fun getChatroomMessageReference(chatroomId: String): CollectionReference {
        return getChatroomReference(chatroomId).collection("chats")
    }

    fun getChatroomId(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            "$userId1 _$userId2" // Add space between userId1 and underscore
        } else {
            "$userId2 _$userId1" // Add space between userId2 and underscore
        }
    }


    fun allChatroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("chatrooms")
    }

    fun getOtherUserFromChatroom(userIds: List<String>): DocumentReference {
        return if (userIds[0] == currentUserId()) {
            // Return the second user ID
            allUserCollectionReference().document(userIds[1])
        } else {
            // Not the current user, return the first user
            allUserCollectionReference().document(userIds[0])
        }
    }

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("HH:mm").format(timestamp.toDate())
    }
}
