:- dynamic sol_part/2. 

hamilton(NN, X, Path):- NN1 is NN-1, hamilton_path(NN1, X, X, [X],Path). 
edge_ex1(a,b). 
edge_ex1(b,c).
edge_ex1(a,c). 
edge_ex1(c,d). 
edge_ex1(b,d). 
edge_ex1(d,e). 
edge_ex1(e,a).
is_edge(X,Y):- edge_ex1(X,Y); edge_ex1(Y,X). 

hamilton_path(0, X, Y , PPath, [Y|PPath]):- 
    is_edge(X, Y).    
hamilton_path(NN, X, Y, PPath, FPath) :-
    NN > 0,
    is_edge(X, Z), !,
    not(member(Z, PPath)),
    NN1 is NN - 1,
    hamilton_path(NN1, Z, Y, [Z|PPath], FPath).

is_edge2(X,Y):- edge_ex2(X,Y); edge_ex2(Y,X). 

edge_ex2(a,b). 
edge_ex2(b,e). 
edge_ex2(c,a). 
edge_ex2(d,c). 
edge_ex2(e,d).

euler(NN, X, Path):- NN1 is NN-1, euler_path(NN1, X, X, [],Path). 


euler_path(0, X, Y , PPath, [[X, Y] | PPath]):- 
    is_edge2(X, Y).    
euler_path(NN, X, Y, PPath, FPath) :-
    NN > 0,
    is_edge2(X, Z),
    not(member([X, Z], PPath)),
    NN1 is NN - 1,
    euler_path(NN1, Z, Y, [[X, Z]|PPath], FPath).

