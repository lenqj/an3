append1([], L2, L2).
append1([H|T], L2, [H|CoadaR]) :- append1(T, L2, CoadaR). 


append3([], [], L3, L3).
append3([H | T], L2, L3, [H | R]) :- append3(T, L2, L3, R).
append3([], [H | T], L3, [H | R]) :- append3([], T, L3, R).

add_first(X, L, R) :- R = [X | L].


sum_fwd([], Acc, Acc).
sum_fwd([H|T], Acc, S) :- Acc1 is (H + Acc), sum_fwd(T, Acc1, S).
sum_fwd(L, S) :- sum_fwd(L, 0, S).


sum_bwd([], 0).
sum_bwd([H | T], S) :- sum_bwd(T, S1), S is S1 + H.


separate_parity([], [], []).
separate_parity([H | T], [H | E], O) :- H1 is (H mod 2), H1 = 0, separate_parity(T, E, O).
separate_parity([H | T], E, [H | O]) :- H1 is (H mod 2), H1 = 1, separate_parity(T, E, O).


remove_duplicates([], []). 
remove_duplicates([H | T], R) :- member(H, T), R = [T | R], remove_duplicates(T, R).






