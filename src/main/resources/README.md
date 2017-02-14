# `src/main/resources`

Copy any files your project needs here. For example, if your code neads to read a `constants.txt` file, copy the file to `src/main/resources/constants.txt`. To read the file in your code, do:
```java
InputStream file = getClass().getResourceAsStream("/constants.txt");
```
