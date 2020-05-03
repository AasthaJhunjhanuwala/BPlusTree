#Aastha Jhunjhanuwala
#UFID: 55271081
#make command will create executable by making all classes

all: Node BPlusImpl bplustree

Node: 		Node.java
			javac Node.java
BPlusImpl:	BPlusImpl.java	
			javac BPlusImpl.java
bplustree:	bplustree.java
			javac bplustree.java

clean:		rm -rf *.class