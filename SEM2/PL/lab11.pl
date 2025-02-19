:- dynamic nod_vizitat_depth/2.
:- dynamic nod_vizitat/1.

:- dynamic depth_max/1.

edge_ex1(a,b). 
edge_ex1(a,c). 
edge_ex1(b,d). 
edge_ex1(d,e). 
edge_ex1(c,f). 
edge_ex1(e,g). 
edge_ex1(f,h). 

dfs(X,_) :- df_search(X). 
dfs(_,L) :- !, collect_reverse([], L). 
df_search(X):-    
  asserta(nod_vizitat(X)),         
    is_edge(X,Y),         
    not(nod_vizitat(Y)),    
    df_search(Y).  
collect_reverse(L, P):-    
    retract(nod_vizitat(X)), !,        
    collect_reverse([X|L], P). 
collect_reverse(L,L). 
depth_max(2).

collect_reverse1(L, P):-    
    retract(nod_vizitat_depth(X, _)), !,        
    collect_reverse1([X|L], P). 
collect_reverse1(L,L). 

is_edge(X,Y):- edge_ex1(X,Y);edge_ex1(Y,X).


dls(X, _) :- depth_max(Dmax), dls_aux(X, 0, Dmax).
dls(_, L) :- !, collect_reverse1([], L).
dls_aux(X, CurrentDepth, MaxDepth) :- 
    CurrentDepth =< MaxDepth,
	asserta(nod_vizitat_depth(X, CurrentDepth)),
	is_edge(X, Y),
	Depth1 is CurrentDepth + 1,
    (not(nod_vizitat_depth(Y, _));
	nod_vizitat_depth(Y, D), 
    D > Depth1,
	retract(nod_vizitat_depth(Y,D)), !),                    
	dls_aux(Y,Depth1,MaxDepth).