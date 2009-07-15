#!/bin/bash
# check directory arguments $1 and $2
# check if web-images and fine-images exist and create
#
# unartig: index images?
# usage arg1=from arg2=to


# go through all directories in the first argument
for file in $1/*;
  do
  echo copy all files in $file; 
  echo to ${2}/fine-images/`basename $file`/fine/
  # go to $file/fine
  # copy or move all JPG and jpg to $2/fine-images/[last part of $1]/fine/.
  mkdir ${2}/fine-images/`basename $file`
  mkdir ${2}/fine-images/`basename $file`/fine
  cp $file/fine/*.jpg ${2}/fine-images/`basename $file`/fine/.
  cp $file/fine/*.JPG ${2}/fine-images/`basename $file`/fine/.
  # echo ${2}/fine-images/`basename $file`/fine
  # go to $file/display
  mkdir ${2}/web-images/`basename $file`
  mkdir ${2}/web-images/`basename $file`/display
  cp $file/display/*.jpg ${2}/web-images/`basename $file`/display/.
  cp $file/display/*.JPG ${2}/web-images/`basename $file`/display/.
  
  mkdir ${2}/web-images/`basename $file`
  mkdir ${2}/web-images/`basename $file`/thumbnail
  cp $file/thumbnail/*.jpg ${2}/web-images/`basename $file`/thumbnail/.
  cp $file/thumbnail/*.JPG ${2}/web-images/`basename $file`/thumbnail/.
done;

