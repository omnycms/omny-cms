mkdir output

cd omny-all/target
zip -r ../../output/omny-$1.zip AllInOne-1.0.jar lib
cd ../.. 
cd ui/omny-angular
zip -r ../../output/omny-$1.zip public_html
cd ../..
zip -r ./output/omny-$1.zip ./default-db-values
cd static
zip -r ../output/omny-$1.zip * 
