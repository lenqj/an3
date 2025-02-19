union([], L, L). 
union([H|T], L2, R) :- member(H, L2), !, union(T, L2, R). 
union([H|T], L2, [H|R]) :- union(T, L2, R).

intersection([], _, []). 
intersection([H|T], L2, [H|R]) :- member(H, L2), !, intersection(T, L2, R). 
intersection([_|T], L2, R) :- intersection(T, L2, R). 


diff([], _, []). 
diff([H|T], L2, R) :- member(H, L2), !, diff(T, L2, R). 
diff([H|T], L2, [H|R]) :- diff(T, L2, R).

min2([H|T], M) :- min2(T, M), M<H, !. 
min2([H|_], H).  

max2([H|T], M) :- max2(T, M), M>H, !. 
max2([H|_], H). 


del_min(L, R) :- min2(L, MIN), delete(L, MIN, R).
del_max(L, R) :- max2(L, MAX), delete(L, MAX, R).


reverse1([], []). 
reverse1([H|T], R) :- reverse1(T, Rcoada), append(Rcoada, [H], R). 

reverse_k([], _, []).
reverse_k([H | T], K, [H | R]) :- K1 is (K-1), K1 >= 0, !, reverse_k(T, K1, R).
reverse_k(L, 0, R) :- reverse1(L, R).


rle_encode([[L1, L2] | [[L1, _] | TT]], R) :- CNT is L2 + 1, R = [[L1, CNT] | R], rle_encode([[L1, CNT] | [TT]], R).
