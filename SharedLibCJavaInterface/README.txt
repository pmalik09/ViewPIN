How to create the Shared Library for Java Native Interface on linux Box

File Descritpion.

1.Common.h file include the declaration of JNI.
2.cryptoki and ctalias standard header files.
3.libcryptio.a and libCryptioki2.so are Library used to build shared library having JNI.
4.mofn.c  and mofn.h are implementation and header files for main function.
5.README describe the steps to build shared library.


1. compilation.
	mofn.c file includes the Java Native Interface implementation.
	gcc -c -I /opt/j2sdk1.4.1_07/include/ -I /opt/j2sdk1.4.1_07/include/linux/ -DOS_UNIX -DDEBUG -DCRYPTOKI_201 mofn.c

2. Linking (creating shared lib)
	ld -G mofn.o -o libMofNCJavaInterface.so libCryptoki2.so libcrypto.a -lm -lc -lpthread

3. compress.
	compress the shared library libMofNCJavaInterface.so using zip utility.

4. ctp.
	transfer this file to luna box using ctp.