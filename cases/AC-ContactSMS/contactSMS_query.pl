inputPin(P, PIN),
setof(R, nodeCharacteristic(P, 'Roles (_g8Baw0NEEeq3NrD2DjPidQ)', R), ROLES),
setof_characteristics(P, PIN, 'AccessRights (_fCiJk0NEEeq3NrD2DjPidQ)', REQ, S),
intersection(REQ, ROLES, []).