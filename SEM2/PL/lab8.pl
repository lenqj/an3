convertIL2CL(L, []) :- var(L),  !.
convertIL2CL([H | T], [H | R]) :- convertIL2CL(T, R).

convertCL2IL([], _) :- !.
convertCL2IL([H | T], [H | R]) :- convertCL2IL(T, R).    

append_il(L1, L2, L2) :- var(L1), !.
append_il([], L2, L2).
append_il([H | T], L2, H | R1) :- append_il(T, L2, R1).

reverse_il_fwd(L, _) :- var(L), !.
reverse_il_fwd([H | T], R) :- reverse_il_fwd(T, R1), append_il(R1, [H | _], R).

reverse_il_bwd(L, Acc, Acc) :- var(L), !.
reverse_il_bwd([H | T], Acc, R) :- append_il([H | _], Acc, Acc1), reverse_il_bwd(T, Acc1, R).
reverse_il_bwd(L, R) :- reverse_il_bwd(L, _, R).

flat_il(L, []) :- var(L), !.
flat_il([H | T], R) :- flat_il(T, R1), append_il(R1, H, R).

incomplete_tree(t(7, t(5, t(3, _, _), t(6, _, _)), t(11, _, _))). 
complete_tree(t(7, t(5, t(3, nil, nil), t(6, nil, nil)), t(11, nil, nil))). 
 
convertIT2CT(incomplete_tree(K, L, R), complete_tree(K, nil, R)) :- var(L).
convertIT2CT(incomplete_tree(K, L, R), complete_tree(K, L, nil)) :- var(R).
convertIT2CT(incomplete_tree(_, L, _), Res) :- convertIT2CT(L, Res).
convertIT2CT(incomplete_tree(_, _, R), Res) :- convertIT2CT(R, Res).

preorder_it(L, _) :- var(L). 
preorder_it(t(K,L,R), List):-  
    preorder_it(L,LL),  
    preorder_it(R, LR),  
    append_il([K|LL], LR, List). 

