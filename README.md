# GenJavaClass
A small tool to generate java source file from json.

Please use maven to build.

`
mvn package
`

After building, jar files will exist in target directory. 
You can use GenJavaClass-[Version]-jar-with-dependencies.jar to execute generation, e.g, 

`
java -jar target/GenJavaClass-1.0-SNAPSHOT-jar-with-dependencies.jar Main -i test.json 
`

test.json is input file.

Help information,
> usage: genJavaClass [-h] [-i <FILE/DIR>]

>  -h,--help       print this message

>  -i <FILE/DIR>   read json file
