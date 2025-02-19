
count_atomic([], 0).
count_atomic([H | T], R) :- atomic(H), !, count_atomic(T, R1), R is R1 + 1.
count_atomic([H | T], R) :- count_atomic(H, R1), count_atomic(T, R2), R is R1 + R2.


sum_atomic([], 0).
sum_atomic([H | T], R) :- atomic(H), !, sum_atomic(T, R1), R is R1 + H.
sum_atomic([H | T], R) :- sum_atomic(H, R1), sum_atomic(T, R2), R is R1 + R2.


member_deterministic(H, [H|_]):- !.
member_deterministic(X, [H|_]):- member_deterministic(X,H).
member_deterministic(X, [_|T]):- member_deterministic(X,T). 



replace(1, a, [[[[1,2], 3, 1], 4],1,2,[1,7,[[1]]]], R). 

