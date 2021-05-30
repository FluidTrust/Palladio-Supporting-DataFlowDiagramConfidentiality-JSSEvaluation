actor(A),
inputPin(A,PIN),
nodeCharacteristic(A, 'EmployeeLocation (_j_v1Y-JAEeqO9NqdRSqKUA)', SUBJ_LOC),
nodeCharacteristic(A, 'EmployeeRole (_nNduk-JAEeqO9NqdRSqKUA)', SUBJ_ROLE),
characteristic(A, PIN, 'CustomerLocation (_h6k4o-JAEeqO9NqdRSqKUA)', OBJ_LOC, S),
characteristic(A, PIN, 'CustomerStatus (_lmMOw-JAEeqO9NqdRSqKUA)', OBJ_STAT, S),
(
	SUBJ_LOC \= OBJ_LOC,
	SUBJ_ROLE \= 'Manager (_dvk30OJAEeqO9NqdRSqKUA)';
	OBJ_STAT = 'Celebrity (_hCxt8OJAEeqO9NqdRSqKUA)',
	SUBJ_ROLE \= 'Manager (_dvk30OJAEeqO9NqdRSqKUA)'
).