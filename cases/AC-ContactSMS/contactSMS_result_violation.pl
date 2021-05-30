?- inputPin(P, PIN),
|    setof(R, nodeCharacteristic(P, 'Roles (_g8Baw0NEEeq3NrD2DjPidQ)', R), ROLES),
|    setof_characteristics(P, PIN, 'AccessRights (_fCiJk0NEEeq3NrD2DjPidQ)', REQ, S),
|    intersection(REQ, ROLES, []).
P = 'sendSMS (_r7sbuVGaEeqNss2H0GFEyA)',
PIN = 'input2 (_ThwmMOODEeqO9NqdRSqKUA)',
ROLES = ['SMSManager (_tKef0FGWEeqNss2H0GFEyA)'],
REQ = ['User (_pwUtIFGWEeqNss2H0GFEyA)', 'ContactManager (_sW8xIFGWEeqNss2H0GFEyA)'],
S = ['contact direct (_9h994OePEeqYs_tmlYiweg)', ['contacts (_ley0SlGbEeqNss2H0GFEyA)', ['contact (_oEvP6lGoEeqNss2H0GFEyA)']], ['criteria (_ihv0WlGbEeqNss2H0GFEyA)'|_20674]] ;
P = 'SMSGateway (__7pjBVGaEeqNss2H0GFEyA)',
PIN = 'sms (_Ds8DUFGbEeqNss2H0GFEyA)',
ROLES = ['SMSManager (_tKef0FGWEeqNss2H0GFEyA)'],
REQ = ['User (_pwUtIFGWEeqNss2H0GFEyA)'],
S = ['sms (_vy8kqlGbEeqNss2H0GFEyA)', ['message (_qe7tWlGbEeqNss2H0GFEyA)', ['contacts (_MZwCelGbEeqNss2H0GFEyA)'|_23968]], ['contact direct (_9h994OePEeqYs_tmlYiweg)', ['contacts (_ley0SlGbEeqNss2H0GFEyA)', ['contact (_oEvP6lGoEeqNss2H0GFEyA)']], ['criteria (_ihv0WlGbEeqNss2H0GFEyA)'|_24016]]] ;
false.