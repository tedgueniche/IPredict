all:
	javac -cp src src/ca/ipredict/controllers/MainController.java

run: all
	java -cp src ca.ipredict.controllers.MainController ./datasets
