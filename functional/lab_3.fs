open System

let explode (s:string) =
  [for c in s -> c]

type Token =
  | OpenBrace | CloseBrace
  | OpenBracket | CloseBracket
  | Colon | Comma
  | String of string
  | Number of int
  | Boolean of bool
  | Null

let tokenize source =
  let rec parseString acc = function
    | '\\' :: '"' :: t -> parseString (acc + "\"") t
    | '\\' :: 'n' :: t -> parseString (acc + "\n") t
    | '"' :: t -> acc, t
    | c :: t -> parseString (acc + c.ToString()) t
    | _ -> failwith "Malformed string."
 
  let rec token acc = function
    | (x :: _) as t when List.exists ((=)x) [')'; ':'; ','; ']'] -> acc, t
    | w :: t when Char.IsWhiteSpace(w) -> acc, t
    | [] -> acc, [] // end of list terminates
    | c :: t -> token (acc + (c.ToString())) t

  let rec tokenize' acc = function
    | w :: t when Char.IsWhiteSpace(w) -> tokenize' acc t
    | '{' :: t -> tokenize' (OpenBrace :: acc) t
    | '}' :: t -> tokenize' (CloseBrace :: acc) t
    | '[' :: t -> tokenize' (OpenBracket :: acc) t
    | ']' :: t -> tokenize' (CloseBracket :: acc) t
    | ':' :: t -> tokenize' (Colon :: acc) t
    | ',' :: t -> tokenize' (Comma :: acc) t
    | '"' :: t -> // start of string
      let s, t' = parseString "" t
      tokenize' (String s :: acc) t'    
    | 'n' :: 'u' :: 'l' :: 'l' :: t -> tokenize' (Null :: acc) t
    | 't' :: 'r' :: 'u' :: 'e' :: t -> tokenize' (Boolean true :: acc) t
    | 'f' :: 'a' :: 'l' :: 's' :: 'e' :: t -> tokenize' (Boolean false :: acc) t
    | d :: t -> // остались числа
      let n, t' = token (d.ToString()) t
      tokenize' (Number (try Convert.ToInt32 n with e -> 0)  :: acc) t'
    | [] -> List.rev acc
    | _ -> failwith "Tokinzation error"
  tokenize' [] source

type JSON =
  | Object of (string * JSON) list
  | Array of JSON list
  | Number of int
  | String of string
  | Boolean of bool
  | Null

let rec parse json =
  let rec parse' json =
    let rec parseObject list = function
      | CloseBrace :: t -> (Object (List.rev list)), t
      | Comma :: t -> parseObject list t
      | Token.String s :: Colon :: t ->
        let a, t = parse' t
        parseObject ((s, a) :: list) t
      | _ -> failwith "Incorrect object"
    let rec parseArray list = function
      | CloseBracket :: t -> (Array (List.rev list)), t
      | Comma :: t -> parseArray list t
      | ob -> 
        let a, t = parse' ob
        parseArray (a :: list) t  
    match json with
      | OpenBrace :: t -> parseObject [] t
      | OpenBracket :: t -> parseArray [] t
      | Token.Null :: t -> JSON.Null, t
      | Token.String s :: t -> JSON.String s, t
      | Token.Number s :: t -> JSON.Number s, t
      | Token.Boolean s :: t -> JSON.Boolean s, t
      | _ -> failwith "Incorrect identification"
  match parse' json with
    | res, [] -> res
    | _ -> failwith "Wrong JSON structure"


let s = """
{
  "a": 1
  "b": {
    "c": [1,2,3]
  }
}
"""

let tree = s |> explode |> tokenize |> parse

let rec maxDeg(node: JSON):int = 
    match node with
        |JSON.Null -> 0
        |JSON.Number _ -> 0
        |JSON.String _ -> 0
        |JSON.Boolean _ -> 0
        |JSON.Array list -> 
              max (list |> List.length) (list |> List.map (maxDeg) |> List.max)
        |JSON.Object elem ->  max (elem |> List.length) (elem |> List.map (snd >> maxDeg) |> List.max)


open System
open System.IO
open System.Net
open System.Text
open System.Collections.Specialized

// почтовый адрес
let email = "salnikov.dmitri@gmail.com"

let rec deserialize element =
    match element with
        |JSON.Null -> " null "
        |JSON.Number x -> x.ToString() 
        |JSON.String str -> " \" " + str + " \" "
        |JSON.Boolean bool -> bool.ToString()
        |JSON.Array list -> 
              " [ " + (list |> List.map (deserialize) |> String.concat ", ") + " ] " 
        |JSON.Object elem -> " { " + (elem |> List.map (fun x -> " \" " + fst(x) + " \" " + " : " + deserialize(snd(x))) |> String.concat " , ") + " } "  


let stringify = 
    deserialize
 

let randomStr = 
    let chars = "ABCDEFGHIJKLMNOPQRSTUVWUXYZ0123456789"
    let charsLen = chars.Length
    let random = System.Random()

    fun len -> 
        let randomChars = [|for i in 0..len -> chars.[random.Next(charsLen)]|]
        new System.String(randomChars)


let rec generate n = 
  let rnd = new Random()
  match n with
    | 0 -> JSON.Object []
    | 1 -> JSON.Array []
    | 2 -> JSON.Number(rnd.Next(12))
    | 3 -> JSON.Array [for i in 1..10 -> JSON.Object [(randomStr(rnd.Next(20)), generate (max (0) (n-1)) )]]
    | _ -> JSON.Object [for i in 1..10 -> (randomStr(rnd.Next(69)), generate(max 0 (n - 1)))]
 
    
let lab3 = 
    [for i in 1..10 -> generate 4 |> stringify |> explode |> tokenize |> parse]


let main () = 
  let values = new NameValueCollection()
  values.Add("email", email)
  values.Add("content", File.ReadAllText(__SOURCE_DIRECTORY__ + @"/" + __SOURCE_FILE__))

  let client = new WebClient()
  let response = client.UploadValues(new Uri("http://mipt.eu01.aws.af.cm/lab3"), values)
  let responseString = Text.Encoding.Default.GetString(response)

  printf "%A\n" responseString