{ system ? builtins.currentSystem }:
let
  nixpkgs = import <nixpkgs> { inherit system; };
  src = fetchTarball "https://github.com/numtide/devshell/archive/master.tar.gz";
  devshell = import src { inherit system nixpkgs; };
in
devshell.mkShell ({pkgs, ...}: {
  packages = with pkgs; [
    git
    openjdk17
    (gradle.override { java = openjdk17; })
    act
  ];
})
