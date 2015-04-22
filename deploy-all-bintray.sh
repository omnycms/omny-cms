apikey=$1
version=$2
cd libraries
for d in */ ; do
    package=${d%/}
    echo $package
    cd $package
    cd ..
    ../deploy-bintray.sh $package/target/$package-$version.jar $package/pom.xml $apikey $version $package
done
#./deploy-bintray.sh lib/omny-auth/target/omny-auth-$version.jar lib/omny-auth/pom.xml  
