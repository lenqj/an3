max1([H|T], M) :- max1(T, M), M>H, !. 
max1([H|_], H). 

delete1(X, [X|T], T) :- !. 
delete1(X, [H|T], [H|R]) :- delete1(X, T, R).
delete1(_, [], []). 

sel_sort_max(L, [M|R]):- max1(L, M), delete1(M, L, L1), sel_sort_max(L1, R). 
sel_sort_max([], []).


ins_sort([H|T], R):- ins_sort(T, R1), insert_ord(H, R1, R). 
ins_sort([], []). 

insert_ord(X, [H|T], [H|R]):-X>H, !, insert_ord(X, T, R). 
insert_ord(X, T, [X|T]). 

insert_ord1(X, [H|T], [H|R]):-X<H, !, insert_ord1(X, T, R). 
insert_ord1(X, T, [X|T]). 

ins_sort_fwd([], Acc, Acc).
ins_sort_fwd([H | T], Acc, R) :- insert_ord1(H, Acc, Acc1), ins_sort_fwd(T, Acc1, R).
ins_sort_fwd(L, R) :- ins_sort_fwd(L, [], R).



bubble_sort(L,R):- one_pass(L,R1,F), nonvar(F), !, bubble_sort(R1,R). 
bubble_sort(L,L). 


one_pass([H1,H2|T], [H2|R], F):- H1>H2, !, F=1, one_pass([H1|T],R,F). 
one_pass([H1|T], [H1|R], F):- one_pass(T, R, F). 
one_pass([], [] ,_). 


bubble_sort_fixed(L, K, R) :- one_pass(L,R1,F), nonvar(F), K1 is K - 1, K1 >= 0, !, bubble_sort_fixed(R1, K1, R). 
bubble_sort_fixed(L, 0, L). 


minchars([H|T], M) :- minchars(T, M), char_code(M, M1), char_code(H, H1), M1<H1, !. 
minchars([H|_], H). 

sort_chars(L, [M|R]):- minchars(L, M), delete1(M, L, L1), sort_chars(L1, R). 
sort_chars([], []).

maxlistlength([H|T], M) :- maxlistlength(T, M), length(M, M1), length(H, H1), M1<H1, !. 
maxlistlength([H|_], H). 

sort_lens(L, [M|R]) :- maxlistlength(L, M), delete1(M, L, L1), sort_lens(L1, R). 
sort_lens([], []). 