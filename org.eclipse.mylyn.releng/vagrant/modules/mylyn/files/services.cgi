#!/usr/bin/perl -w
use strict;
use warnings;
use CGI;
use File::Find::Rule;

my $cgi = new CGI;
my $buffer = $cgi->query_string();
my $callback = $cgi -> param('callback');
my $muster = 'service*.json';
my @verzeichnisse = (
    '/home/tools',
);

my @files = File::Find::Rule->file()
    ->mindepth(3)
    ->maxdepth(4)
    ->name( $muster )
    ->in( @verzeichnisse );

print "Content-type:application/json\r\n\r\n";
if ($callback) {
  print "$callback(";
}
print "[\n";
my $c=0;
foreach my $file (@files) {
  open(INFILE, "< $file");
  my @filedata=<INFILE>;
  close(INFILE);
  if ($c>0) {
    print ",\n";
  }
  foreach my $fd (@filedata)
  {
    print "$fd";
  }
  $c= $c + 1;
}
print "]";
if ($callback) {
  print ")";
}
