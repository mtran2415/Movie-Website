# CS 122B Project 4 Autocomplete Example

This example shows how autocomplete search can be implemented.

### To run this example: 
1. clone this repository using `git clone https://github.com/UCI-Chenli-teaching/autocomplete-example.git`
2. open Eclipse -> File -> import -> under "Maven" -> "Existing Maven Projects" -> Click "Finish".
3. For "Root Directory", click "Browse" and select this repository's folder. Click "Finish".
4. You can then run this project on Tomcat server.

### Brief Explanation
`index.html` is the main page. It only contains an input box. It also includes the javascript library files.

`index.js` is the main Javascript file to use this jQuery autocomplete library to implement autocomplete search: https://github.com/devbridge/jQuery-Autocomplete

`HeroSuggestion.java` is a Java servlet searches superhero names and return suggestions in JSON format.

