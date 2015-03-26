#include <stdio.h>
#include "jni.h"
#include "cryptoki.h"
#include "Common.h"
#include "mofn.h"
#include <assert.h>

#include <assert.h>

#define ckCharToJChar(x) (jchar) x
#define ckULongToJSize(x) (jsize) x
#define CLASS_OUT_OF_MEMORY_ERROR "java/lang/OutOfMemoryError"

JNIEXPORT jlong JNICALL Java_com_safenetinc_Common_MofnAuthentication
  (JNIEnv *env, jobject obj, jlong jSessionHandle)
{
        CK_SESSION_HANDLE ckSessionHandle;
        jlong jObjectHandle;
        CK_RV rv;
        ckSessionHandle = jLongToCKULong(jSessionHandle);
        rv = MofN_Authentication(ckSessionHandle);
        jObjectHandle = ckULongToJLong(rv);
        return jObjectHandle;
}



CK_RV MofN_Authentication (CK_SESSION_HANDLE hSession)
{
        CA_MOFN_STATUS MofNStatus;
        CK_SESSION_INFO info;
        CK_RV rv;

        //fprintf(stdout, "In Mofn Auth function");
        rv = C_GetSessionInfo(hSession, &info);
        if (rv != CKR_OK) {
                fprintf(stdout, "C_GetSessionInfo: rv = 0x%.8X\n", rv);
                return rv;
        }

        rv = CA_GetMofNStatus( info.slotID, &MofNStatus );
        if (rv != CKR_OK) {
                fprintf(stdout, "CA_ActivateMofN: rv = 0x%.8X\n", rv);
                return rv;
        }

        /* Make sure the token is MofN & MofN generated*/
        if (!( MofNStatus.ulFlag & CAF_M_OF_N_REQUIRED) )
        {
                rv = CAF_M_OF_N_REQUIRED;
                fprintf(stdout, "MofN required rv = 0x%.8X\n",rv);
                return rv;
                /* not initialized with CA_SetMofN */
        }
        else if (!( MofNStatus.ulFlag & CAF_M_OF_N_GENERATED ))
        {
                rv = CAF_M_OF_N_GENERATED;
                fprintf(stdout, "MofN required rv = 0x%.8X\n",rv);
                return rv;

        }
        else
        {
                //fprintf(stdout,"Perform MofN authentication\n");
                rv = CA_ActivateMofN(hSession, NULL, 0);
                if (rv != CKR_OK) {
                        fprintf(stdout, "CA_ActivateMofN: rv = 0x%.8X\n", rv);
                        return rv;
                }
        }
        return rv;
}

void throwOutOfMemoryError(JNIEnv *env)
{
        jclass jOutOfMemoryErrorClass;
        jmethodID jConstructor;
        jthrowable jOutOfMemoryError;

        jOutOfMemoryErrorClass = (*env)->FindClass(env, CLASS_OUT_OF_MEMORY_ERROR);
        assert(jOutOfMemoryErrorClass != 0);

        jConstructor = (*env)->GetMethodID(env, jOutOfMemoryErrorClass, "<init>", "()V");
        assert(jConstructor != 0);
        jOutOfMemoryError = (jthrowable) (*env)->NewObject(env, jOutOfMemoryErrorClass, jConstructor);
        (*env)->Throw(env, jOutOfMemoryError);
}

jcharArray ckCharArrayToJCharArray(JNIEnv *env, const CK_CHAR_PTR ckpArray, CK_ULONG ckLength)
{
        CK_ULONG i;
        jchar* jpTemp;
        jcharArray jArray;

        jpTemp = (jchar*) malloc(ckLength * sizeof(jchar));
  if (jpTemp == NULL) { throwOutOfMemoryError(env); return NULL; }
        for (i=0; i<ckLength; i++) {
                jpTemp[i] = ckCharToJChar(ckpArray[i]);
        }
        jArray = (*env)->NewCharArray(env, ckULongToJSize(ckLength));
        (*env)->SetCharArrayRegion(env, jArray, 0, ckULongToJSize(ckLength), jpTemp);
        free(jpTemp);

        return jArray ;
}

JNIEXPORT jcharArray JNICALL Java_com_safenetinc_Common_GetPass
  (JNIEnv *env, jobject jobj){

    char * password = NULL;

    jcharArray jPassword;
    password = getpass("Enter Partition Secret:");
    jPassword = ckCharArrayToJCharArray(env, (CK_CHAR_PTR)(password), strlen(password));
}
