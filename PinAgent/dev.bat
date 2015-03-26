ctp -pw temp123# PinAgent.zip admin@172.25.12.128:

plink -ssh -pw temp123# admin@172.25.12.128 spa f i -f PinAgent.zip

plink -ssh -pw temp123# admin@172.25.12.128 sp Dep -file PinAgent.war -virtualHost pinagent.mybank.com

rem plink -ssh -pw temp123# admin@172.25.12.128 spa restart

rem plink -ssh -pw temp123# admin@172.25.12.128 spa i l

