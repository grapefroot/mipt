// Learn more about F# at http://fsharp.org
// See the 'F# Tutorial' project for more help.
open System
open System.IO
open ilf.pgn
open ilf.pgn.Data
open System.Threading

//open and process data


let map' func items = 
    let tasks =
        seq {
                for i in items -> async {
                return (func i)
            }
        }
    Async.RunSynchronously (Async.Parallel tasks)

let map2' func items1 items2 = 
    let tasks =
        seq {
                for i in (List.zip items1 items2) -> async {
                return (func i)
            }
        }
    Async.RunSynchronously (Async.Parallel tasks)



let reader = new PgnReader()
let gameDb = reader.ReadFromFile(@"C:\Users\Dmitriy\Downloads\ficsgamesdb_201501_standard2000_nomovetimes_1237064.pgn");
let games = gameDb.Games

let moves = games.ConvertAll(fun x -> x.MoveText.GetMoves())
let results = games.ConvertAll(fun x -> x.Result)
let converted_results = List.ofSeq results
let converted = List.ofSeq moves
let to_f_sharp_lists = converted |> map' (fun x -> List.ofSeq x)

let FreqDict S =  
    Seq.fold (
        fun(ht:Map<_, int>) v -> 
            if Map.containsKey v ht then Map.add v ((Map.find v ht)+1) ht
            else Map.add v 1 ht)  
            (Map.empty) S

let enumerate x =
    x |> List.mapi (fun i x -> (i, x))
 

let finalized1 = to_f_sharp_lists |> map' (fun x -> String.Join(",", x).Split(',') |> Array.toList)

let whitePlays = [for item in finalized1 -> (enumerate item) |> List.filter (fun i -> fst (i) % 2 = 0) |> List.unzip |> snd]

let blackPlays = [for item in finalized1 -> (enumerate item) |> List.filter (fun i -> fst (i) % 2 = 1) |> List.unzip |> snd]

let whitePlaysResults = List.zip whitePlays converted_results

let blackPlaysResults = List.zip blackPlays converted_results   

let whiteacc = [for item in whitePlaysResults do
                    for j in 1..(fst item).Length-> (fst (List.splitAt j (fst item)), snd item)]
                    |> FreqDict |> Map.toList |> List.filter (fun x -> snd x > 1) 

let newlist = [for item in (whiteacc |> List.unzip |> fst |> List.unzip |> fst) -> whiteacc |> List.filter(fun ((x,y),z) ->  x = item)]

let allwins = newlist |> map' (fun x -> List.fold (fun acc (x,y) -> acc + y) 0 x) 

let calcwins list str = list |> map' (fun x -> x |> List.filter (fun ((x, y), z) -> y.ToString() = str) |> 
map' (fun ((x,y),z) -> z)) |>
map' (fun x -> match x.Length with
                        | 0 -> [0]
                        | _ -> x) |>
List.fold (fun acc y -> acc@y) [] 

let whiteWins = calwins newlist "White"

let calcPercentages list1 = List.map2' (fun (x,y) -> (float(x)/float(y))) list1 allwins 

let whitePercentages = whitewins |> calcPercentages

let blackacc = [for item in blackPlaysResults do
                    for j in 1..(fst item).Length-> (fst (List.splitAt j (fst item)), snd item)]
                    |> FreqDict |> Map.toList |> List.filter (fun x -> snd x > 1) 

let newlist1 = [for item in (blackacc |> List.unzip |> fst |> List.unzip |> fst) -> blackacc |> List.filter(fun ((x,y),z) ->  x = item)]

let blackWins = calcwins newlist1 "Black"

let blackPercentages = blackWins |> calcPercentages


let longestDebut acc = acc |>  List.unzip |> fst 
                    |> List.maxBy (fun x -> List.length (fst x))

let longestDebutWhite = whiteacc |> longestDebut

let longestDebutBlack = blackacc |> longestDebut



