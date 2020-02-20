CKEDITOR.editorConfig = function(config) {
    config.height = 260;
    //config.removePlugins = 'htmlwriter';
    config.toolbar = [{
            name: 'document',
            groups: ['mode', 'document', 'doctools'],
            items: ['Source', '-', 'Templates']
        }, {
            name: 'clipboard',
            groups: ['clipboard', 'undo'],
            items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']
        }, {
            name: 'styles',
            items: ['Format']
        },
        '/', {
            name: 'basicstyles',
            groups: ['basicstyles', 'cleanup'],
            items: ['Bold', 'Italic', 'Underline', '-', 'RemoveFormat']
        }, {
            name: 'paragraph',
            groups: ['list', 'indent', 'blocks', 'align', 'bidi'],
            items: ['NumberedList', 'BulletedList', '-', 'Blockquote', 'CreateDiv']
        }, {
            name: 'links',
            items: ['Link', 'Unlink', 'Anchor']
        }, {
            name: 'insert',
            items: ['Image', 'Table', 'SpecialChar', 'Iframe']
        }, {
            name: 'tools',
            items: ['Maximize', 'ShowBlocks']
        },
    ];
    config.allowedContent = true;
    config.entities = false;
    config.protectedSource.push(/<span[^>]*><\/span>/g);
    config.bodyId = 'ckeditor-body-id';
    config.templates_files = ['/cont/assets/js/ckeditor/ckeditor-template.js'];
    config.templates_replaceContent = false;
    //config.stylesSet = '/cont/assets/js/ckeditor/ckeditor-styles.js';
    config.contentsCss = ['/cont/assets/css/main.min.css', '//maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css'];
};