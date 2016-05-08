--SHOW VARIABLES WHERE Variable_name LIKE '%dir';
SELECT MIN(b.time) FROM secondebidbar b WHERE b.containerId=7;
SELECT MIN(b.time) FROM secondemidpointbar b WHERE b.containerId=7;
SELECT MIN(b.time) FROM secondeaskbar b WHERE b.containerId=7;


DELETE FROM secondebidbar WHERE containerId=7 AND time<=1456334506;
DELETE FROM secondeaskbar WHERE containerId=7 AND time<=1456334506;

					