<img src="https://github.com/idioglossia/sloth-db/blob/main/cover.png?raw=1" />

# Sloth DB [![](https://jitpack.io/v/idioglossia/sloth-db.svg)](https://jitpack.io/#idioglossia/sloth-db)

A slow key value storage in Java, where keys are file names and values are file contents!
No file locks available since two processes are not supposed to work with same Sloth DB storage at same time!
I'm not even sure why would you use it! It just simple writes files in an organized way!

[This Sample](https://github.com/idioglossia/sloth-db/blob/main/src/test/java/Sample.java) can show you how to create a storage and collections.
Currently Sloth DB supports MAP and LIST collections and can story `byte[]` or `String`.
