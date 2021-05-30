inputPin(P, PIN),
setof(R, nodeCharacteristic(P, 'Roles (_JvuuQ9vqEeqNdo_V4bA-xw)', R), ROLES),
setof_characteristics(P, PIN, 'AccessPermissions (_k9jB49vTEeqNdo_V4bA-xw)', REQ, S),
intersection(REQ, ROLES, []).