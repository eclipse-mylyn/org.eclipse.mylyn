update() {
OLD=$1
NEW=$2
find -name plan.xml | xargs sed -i -e "s/$OLD/$NEW/g"
}

update 0.9 1.0
update 0.8 0.9

update 3.7 3.8
update 3.6 3.7

update 1.6 1.7
update 1.5 1.6
