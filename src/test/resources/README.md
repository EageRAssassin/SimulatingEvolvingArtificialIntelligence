# `src/test/resources`

Copy any files your tests need here. For example, if your tests nead to read a `constants.txt` file, copy the file to `src/main/resources/constants.txt`. To read the file in your code, do:
```java
InputStream file = getClass().getResourceAsStream("/constants.txt");
```
