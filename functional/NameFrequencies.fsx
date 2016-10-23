open System
open System.IO
open Microsoft.FSharp.Core

let ReadLines fn = 
    seq { use inp = File.OpenText fn in
          while not(inp.EndOfStream) do
            yield(inp.ReadLine())
        }

let FreqDict S = 
    Seq.fold (
        fun(ht:Map<_, int>) v ->
            if Map.containsKey v ht then Map.add v ((Map.find v ht)+1) ht
            else Map.add v 1 ht)
            (Map.empty) S


let lst = ReadLines @"C:\Users\Dmitriy\Documents\Visual Studio 2015\Projects\Library2\Library2\resource\Book1.txt" |>
            Seq.collect(fun x -> x.Split([|'.'|])) |>
            Seq.toList

//make pairs from list
let rec pairs l =
    match l with
    | [] | [_] -> []
    | h :: t -> 
        [for x in t do
            yield h,x
         yield! pairs t]

//filter empty sentences
let filtered = lst |> List.filter (fun x -> x.Length > 1) 

//split
let splittedList = [for str in lst -> str.Split([|' '|])]

//convert nested elements to list 
let mapped = [for element in splittedList -> Array.toList element] 

//filter lowercase letters
let capitalLetters = [for nested_list in mapped -> nested_list |> List.filter (fun x -> match x.Length with 0 -> false |_ -> Char.IsUpper x.[0])]

//filter out one-named lists
let suitableList = capitalLetters |> List.filter (fun x -> x.Length > 1)

//make pairs from each nested list
let zipped = [for nested_list in suitableList -> pairs nested_list]

//flush pairs to one list
let flattened = [for x in zipped do for y in x -> y]

//calculate frequences
flattened |>
FreqDict |>
Map.toList

