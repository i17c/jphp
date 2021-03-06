--TEST--
unserialize() floats with E notation (#18654)
--INI--
precision=12
serialize_precision=100
--FILE--
<?php
foreach(array(1e2, 5.2e25, 85.29e-23, 9e-9) AS $value) {
	echo ($ser = serialize($value))."\n";
	var_dump(unserialize($ser));
	echo "\n";
}
?>
--EXPECTF--
d:100;
float(100)

d:5.2000000000000E+25;
float(5.2000000000000E+25)

d:8.5290000000000E-22;
float(8.5290000000000E-22)

d:9.0000000000000E-09;
float(9.0000000000000E-09)
