/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles a client-side channel.
 */
@Sharable
public class TelnetClientHandler extends ChannelInboundHandlerAdapter {

    Controllers controller;

    ChannelHandlerContext context;
    Network network;
    private static String downloadPath = Directories.CLIENT_FILES_DIRECTORY.getPath();

    public TelnetClientHandler(Network ourInstance) {
        this.network = ourInstance;
        this.controller = network.getController();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.controller = network.getController();
        this.context = ctx;

        if (msg instanceof List) {
            if (controller instanceof NettyClientWindowController)
                controller.updateServerListSideInFxThread((List<FileInfo>) msg);
        } else if (msg instanceof FilePart) {
            try (FileOutputStream fos = new FileOutputStream(new File(downloadPath+"\\" + ((FilePart) msg).getFileName() + FileUtility.FILENAME_DELIMETER + ((FilePart) msg).getPartFileNumber() + FileUtility.FILENAME_POSTFIX))) {
                fos.write(((FilePart) msg).getPartFile());
            }
            System.out.println("Получена часть файла " + ((FilePart) msg).getFileName());
        } else if (msg instanceof String) {
            String command = (String) msg;
            String[] incommingMessage = command.split(commandLibrary.DELIMETER);
            if (command.startsWith(commandLibrary.FILE_SEND_COMPLETE)) {
                long fileControlLength = Long.parseLong(incommingMessage[3]);
                String destinationPath = incommingMessage[2];
                String fileName = incommingMessage[1];

                List<Path> listPartFiles = Files.list(Paths.get(downloadPath))
                        .filter(path -> path.getFileName().toString().startsWith(fileName))
                        .filter(path -> path.getFileName().toString().endsWith(FileUtility.FILENAME_POSTFIX))
                        .sorted(new Comparator<Path>() {
                            @Override
                            public int compare(Path o1, Path o2) {
                                long i1 = Long.parseLong(o1.toString().split(FileUtility.FILENAME_DELIMETER)[1].split(".p")[0]);
                                long i2 = Long.parseLong(o2.toString().split(FileUtility.FILENAME_DELIMETER)[1].split(".p")[0]);
                                return new Long(i1 - i2).intValue();
                            }
                        }).collect(Collectors.toList());
                File receivedFile = FileUtility.mergeFiles(listPartFiles, new File(destinationPath+"\\" + listPartFiles.get(0).getFileName().toString().split(FileUtility.FILENAME_DELIMETER)[0]));
                if (receivedFile.length() != fileControlLength) {
                    ctx.writeAndFlush(commandLibrary.fileReceivedWithMissingDataMessage(receivedFile.getName()));
                    receivedFile.delete();
                }
                controller.refreshClientSideInFxThread(destinationPath+"\\");
            } else if (command.startsWith(commandLibrary.RECEIVE_FILE)) {

            } else if (command.startsWith(commandLibrary.AUTHORIZE_ACCEPT)) {
                controller.showInformationWindowInFxThread("Login successfully");
                network.setUserId(Long.parseLong(incommingMessage[1]));
                controller.changeScene();
            } else if (command.startsWith(commandLibrary.AUTHORIZE_DENIED))
                controller.showErrorWindowInFxThread("Login or password is wrong");
            else if (command.startsWith(commandLibrary.REGISTRATION_ACCEPT)) {
                controller.showInformationWindowInFxThread("Registration success");
                network.setUserId(Long.parseLong(incommingMessage[1]));
                controller.changeScene();
            } else if (command.startsWith(commandLibrary.REGISTRATION_DENIED))
                controller.showErrorWindowInFxThread("Login is busy");


        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause);
        cause.printStackTrace();
        ctx.close();
    }

    public void sendMessage(FilePart message) {
        this.context.writeAndFlush(message);
    }

    public void sendMessage(Path message) {
        this.context.writeAndFlush(message);
    }

    public void close() {
        this.context.close();
    }


}
