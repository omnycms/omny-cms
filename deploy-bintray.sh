groupid=ca.omny
package=$5
version=$4
group_subdir=`echo $groupid | tr . /`
curl -T $1 -ualamarre:$3 https://api.bintray.com/maven/omny/omny/${package}/${group_subdir}/${package}/${version}/${package}-$version.jar
curl -T $2 -ualamarre:$3 https://api.bintray.com/maven/omny/omny/${package}/${group_subdir}/${package}/${version}/${package}-${version}.pom
