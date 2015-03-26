#ifndef MOFN_H
#define MOFN_H

typedef struct {
CK_ULONG ulID;
CK_ULONG ulM;
CK_ULONG ulN;
CK_ULONG ulSecretSize;
CK_ULONG ulFlag;
} CA_MOFN_STATUS;

typedef CA_MOFN_STATUS * CA_MOFN_STATUS_PTR;

#define CAF_M_OF_N_REQUIRED 0x00000001
#define CAF_M_OF_N_ACTIVATED 0x00000002
#define CAF_M_OF_N_GENERATED 0x00000004
#define CAF_M_OF_N_CLONEABLE 0x00000008

#define jLongToCKULong(x) (CK_ULONG) x
#define ckULongToJLong(x) (jlong) x

CK_RV MofN_Authentication (CK_SESSION_HANDLE hSession);

#endif
