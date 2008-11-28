if [ $# -lt 1 ]
then
  echo "usage: extract-plugins.sh files"
  exit 1
fi

echo == feature.xml ==
echo 
for i in `grep -h "id=\"org.eclipse.mylyn" $* | grep -v feature`; do
 i=`echo $i | sed -e 's/.*id=//' -e 's/\"//g'`
 echo "<plugin id=\"$i.source\" version=\"0.0.0\"/>"
done

echo
echo == build.properties ==
echo
for i in `grep -h "id=\"org.eclipse.mylyn" $* | grep -v feature`; do
 i=`echo $i | sed -e 's/.*id=//' -e 's/\"//g'`
 echo "generate.plugin@$i.source=$i"
done
