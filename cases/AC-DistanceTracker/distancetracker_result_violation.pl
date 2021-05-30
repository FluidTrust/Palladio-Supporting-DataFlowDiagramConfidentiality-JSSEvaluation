?- inputPin(P, PIN),
|    setof(R, nodeCharacteristic(P, 'Roles (_g8Baw0NEEeq3NrD2DjPidQ)', R), ROLES),
|    setof_characteristics(P, PIN, 'AccessRights (_fCiJk0NEEeq3NrD2DjPidQ)', REQ, S),
|    intersection(REQ, ROLES, []).
P = 'recordDistance (_LFSAV1GPEeqgwc19jCODXg)',
PIN = 'input (_6P5g0OMIEeqO9NqdRSqKUA)',
ROLES = ['TrackingService (_JHaiQFGLEeq8MuaPiZgRhQ)'],
REQ = ['User (_GlhNR1GLEeq8MuaPiZgRhQ)', 'DistanceTracker (_Kfr8wFGLEeq8MuaPiZgRhQ)'],
S = ['direct distance (_tebtEOk1EeqV1rV_LnlEIA)', ['locations (_rleKiVGOEeqgwc19jCODXg)']] ;
P = 'DistanceStore (_Y7xPBlGPEeqgwc19jCODXg)',
PIN = 'input (_CBHaYOMJEeqO9NqdRSqKUA)',
ROLES = ['TrackingService (_JHaiQFGLEeq8MuaPiZgRhQ)'],
REQ = ['User (_GlhNR1GLEeq8MuaPiZgRhQ)', 'DistanceTracker (_Kfr8wFGLEeq8MuaPiZgRhQ)'],
S = ['distance (_e82nuVGPEeqgwc19jCODXg)', ['direct distance (_tebtEOk1EeqV1rV_LnlEIA)', ['locations (_rleKiVGOEeqgwc19jCODXg)']]] ;
false.