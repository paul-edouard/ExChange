delete from `ibbar` where `BAR_TYPE`='IbHourBar';
delete from `ibbar` where `BAR_TYPE`='IbDayBar';
delete from `ibbar` where `BAR_TYPE`='IbBarContainer';
drop table ibbarcontainer;
drop table ibbar;