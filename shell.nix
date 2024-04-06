{ system ? builtins.currentSystem }:
let
  nixpkgs = import <nixpkgs> { inherit system; };
  src = fetchTarball "https://github.com/numtide/devshell/archive/master.tar.gz";
  devshell = import src { inherit system nixpkgs; };
in
devshell.mkShell ({pkgs, ...}: {
  packages = with pkgs; [
    git
    jetbrains.jdk
    (gradle.override { java = jetbrains.jdk; })
    act
  ];
})
