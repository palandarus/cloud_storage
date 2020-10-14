/*
 * Copyright 2014 The Netty Project
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

import DataBase.DBHelper;
import DataBase.User;
import io.netty.channel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FileServerHandler extends ChannelInboundHandlerAdapter {
    private static final String SERVER_ROOT_PATH = Directories.SERVER_FILES_DIRECTORY.getPath();
    private static final String SERVER_TEMP_PATH = Directories.SERVER_TEMP_PATH.getPath();
    private boolean fileReceived = false;
    DBHelper dbHelper;
    private static HashMap<Long, Channel> usersMap=new HashMap<>();

    public FileServerHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключился клиент " + ctx.toString());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush("ERR: " +
                    cause.getClass().getSimpleName() + ": " +
                    cause.getMessage() + '\n').addListener(ChannelFutureListener.CLOSE);
        } else ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof Path)
            ctx.writeAndFlush(FileServerManager.getFileList((Path) o));
        else if (o instanceof FilePart) {
            try (FileOutputStream fos = new FileOutputStream(new File("./" + SERVER_TEMP_PATH + "/" + ((FilePart) o).getFileName() + FileUtility.FILENAME_DELIMETER + ((FilePart) o).getPartFileNumber() + FileUtility.FILENAME_POSTFIX))) {
                fos.write(((FilePart) o).getPartFile());

            }
            System.out.println("Получена часть файла " + ((FilePart) o).getFileName());
        } else if (o instanceof String) {
            String command = (String) o;
            String[] incommingMessage = command.split(commandLibrary.DELIMETER);
            if (command.startsWith(commandLibrary.FILE_SEND_COMPLETE)) {
                long fileControlLength = Long.parseLong(incommingMessage[3]);
                String destinationPath = incommingMessage[2];
                String fileName = incommingMessage[1];

                List<Path> listPartFiles = Files.list(Paths.get(SERVER_TEMP_PATH))
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
                File receivedFile = FileUtility.mergeFiles(listPartFiles, new File(destinationPath + listPartFiles.get(0).getFileName().toString().split(FileUtility.FILENAME_DELIMETER)[0]));
                if (receivedFile.length() != fileControlLength) {
                    ctx.writeAndFlush(commandLibrary.fileReceivedWithMissingDataMessage(receivedFile.getName()));
                    receivedFile.delete();
                }
                try {
                    ctx.writeAndFlush(FileServerManager.getFileList(Paths.get(incommingMessage[2])));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if(command.startsWith(commandLibrary.SEND_FILE)) {
                FileInfo transferFile=new FileInfo(Paths.get(incommingMessage[2]+incommingMessage[1]));
                if (transferFile != null) {
                        try {
                            FileUtility.splitAndSend(new File(transferFile.getFullFilePath()), 1024 * 780, ctx.channel() );
                            ctx.writeAndFlush(commandLibrary.sendingFileCompleteMessage(transferFile.getFileName() ,incommingMessage[3], transferFile.getFileLength()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

            }
            else if (command.startsWith(commandLibrary.DELETE_FILE)) {
                File delFile = new File(incommingMessage[2]);
                if (!delFile.delete()) ctx.writeAndFlush(commandLibrary.fileDeleteErrorMessage(delFile.getName()));
                ctx.writeAndFlush(FileUtility.getFileList(incommingMessage[2].replace(incommingMessage[1], "")));
            }
            else if(command.startsWith(commandLibrary.GET_ROOT_FILES_LIST)){
                Long userId=Long.parseLong(incommingMessage[1]);
                ctx.writeAndFlush(FileUtility.getFileList(SERVER_ROOT_PATH+userId.toString()+"\\"));
            }
            else if (command.startsWith(commandLibrary.AUTHORIZE_REQUEST)) {
                User user = new User(incommingMessage[1], incommingMessage[2], SERVER_ROOT_PATH);
                if (user.authorization()) {
                    usersMap.put(user.getId(),ctx.channel());
                    ctx.writeAndFlush(commandLibrary.authorizationAccessMessage(user.getId()));
                    ctx.writeAndFlush(FileUtility.getFileList(user.getUserDirectory()));
                } else
                    ctx.writeAndFlush(commandLibrary.authorizationDeniedMessage(user.getLogin()));
            }
            else if (command.startsWith(commandLibrary.REGISTRATION_REQUEST)) {
                User user = new User(incommingMessage[1], incommingMessage[2], SERVER_ROOT_PATH);
                if (user.createNewUser()) {
                    usersMap.put(user.getId(),ctx.channel());
                    ctx.writeAndFlush(commandLibrary.registrationAccessMessage(user.getId()));
                    FileUtility.createUserFolder(user.getUserDirectory());
                    ctx.writeAndFlush(FileUtility.getFileList(user.getUserDirectory()));
                } else ctx.writeAndFlush(commandLibrary.authorizationDeniedMessage(user.getLogin()));
            }

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client has been diconnected " + ctx);
    }
}

