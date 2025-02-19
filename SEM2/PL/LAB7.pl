%tree1(t(40, t(30, t(25, nil, nil), t(35, nil, nil)), t(50, t(45, nil, nil), t(60, nil, nil)))).
tree1(t(6, t(4,t(2,nil,nil),t(5,nil,nil)), t(9,t(7,nil,nil),nil))). 

ternary_tree(t(6,  t(4,  t(2, nil, nil, nil),  nil,  t(7, nil, nil, nil)),  t(5, nil, nil, nil),  t(9,  t(3, nil, nil, nil), nil, nil) ) ). 
% 1.1. inorder=Left->Root->Middle->Right 
ternary_inorder(t(K, L, M, R), List):-  
    ternary_inorder(L, LL),  
    ternary_inorder(M, LM), 
    ternary_inorder(R, LR), 
    append(LL, [K|LM], List1),
	append(List1, LR, List). 
ternary_inorder(nil, []). 
% 1.2. preorder=Root->Left->Middle->Right 

ternary_preorder(t(K, L, M, R), List):-  
    ternary_preorder(L, LL),  
    ternary_preorder(M, LM), 
    ternary_preorder(R, LR),
    append([K | LL], LM, List1),
	append(List1, LR, List).
ternary_preorder(nil, []). 
% 1.3. postorder=Left->Middle->Right->Root 

ternary_postorder(t(K, L, M , R), List):-  
    ternary_postorder(L,LL), 
    ternary_postorder(M, LM), 
    ternary_postorder(R, LR), 
    append(LL, LM, List1),
	append(List1, LR, List2),
    append(List2, [K], List).
ternary_postorder(nil, []).

pretty_print_ternary(T):- pretty_print_ternary(T, 0). 
pretty_print_ternary(nil, _). 
pretty_print_ternary(t(K, L, M, R), D):-  
    D1 is D+1, 
    print_key(K, D), 
    pretty_print_ternary(L, D1),   
    pretty_print_ternary(M, D1), 
    pretty_print_ternary(R, D1).
print_key(K, D):-
    D>0, !, 
    D1 is D-1, 
    tab(8), 
    print_key(K, D1).
print_key(K, _):- write(K), nl. 

ternary_height(nil, 0).
ternary_height(t(_, L, M, R), H):- 
    ternary_height(L, H1), 
    ternary_height(M, H2), 
    ternary_height(R, H3), 
    max(H1, H2, H12), 
    max(H12, H3, H123),
    H is H123+1. 
max(A, B, A):-
    A>B, !. 
max(_, B, B). 


delete_key(Key, t(Key, L, nil), L):- !.
delete_key(Key, t(Key, nil, R), R):- !.
delete_key(Key, t(Key, L, R), t(Pred,NL,R)):- !, get_pred(L,Pred,NL).
delete_key(Key, t(K,L,R), t(K,NL,R)):- Key<K, !, delete_key(Key,L,NL).
delete_key(Key, t(K,L,R), t(K,L,NR)):- delete_key(Key,R,NR). 

get_pred(t(Pred, L, nil), Pred, L):- !. 
get_pred(t(Key, L, R), Pred, t(Key, L, NR)):- get_pred(R, Pred, NR). 


delete_key_succ(Key, t(Key, L, nil), L):- !. 
delete_key_succ(Key, t(Key, nil, R), R):- !.
delete_key_succ(Key, t(Key, L, R), t(Succ,NL,R)):- !, get_succ(L,Succ,NL).
delete_key_succ(Key, t(K,L,R), t(K,NL,R)):- Key<K, !, delete_key_succ(Key,L,NL). 
delete_key_succ(Key, t(K,L,R), t(K,L,NR)):- delete_key_succ(Key,R,NR). 

get_succ(t(Succ, nil, R), Succ, R):- !. 
get_succ(t(Key, L, R), Succ, t(Key, NL, R)):- get_succ(L, Succ, NL). 


leaf_list(t(K, nil, nil), [K]) :- !.
leaf_list(t(_, TL, TR), R) :- leaf_list(TL, R1), leaf_list(TR, R2), append(R1, R2, R).
leaf_list(nil, []).


height(nil, 0).
height(t(_, L, R), H):- 
    height(L, H1),  
    height(R, H2), 
    max(H1, H2, H3),  
    H is H3+1.

diam(t(_, L, R), D) :-
    diam(L, DL),
    diam(R, DR),
    height(L, HL),
    height(R, HR),
    SUMH is HL + HR + 1,
    max(DL, DR, MAXD),
    max(MAXD, SUMH, D).
diam(nil, 0).


internal_list(t(_, nil, nil), []) :- !.
internal_list(t(K, TL, TR), R) :- internal_list(TL, R1), internal_list(TR, R2), append(R1, [K | R2], R).
internal_list(nil, []).
    


